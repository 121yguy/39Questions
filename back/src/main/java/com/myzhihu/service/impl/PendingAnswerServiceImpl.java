package com.myzhihu.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myzhihu.dao.AnswerDao;
import com.myzhihu.dao.PendingAnswerDao;
import com.myzhihu.domain.dto.PendingAnswerData;
import com.myzhihu.domain.dto.PendingAnswerWithQuestionTitle;
import com.myzhihu.domain.entity.Answer;
import com.myzhihu.domain.entity.PendingAnswer;
import com.myzhihu.service.DraftService;
import com.myzhihu.service.ImageService;
import com.myzhihu.utils.HtmlUtils;
import com.myzhihu.utils.ImagesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("PendingAnswerService")
public class PendingAnswerServiceImpl implements DraftService<PendingAnswer, PendingAnswerWithQuestionTitle> {

    @Autowired
    PendingAnswerDao pendingAnswerDao;

    @Autowired
    AnswerDao answerDao;

    @Autowired
    ImageService imageService;


    @Override
    public boolean addDraft(PendingAnswer draft, int uid, boolean isSubmit) {
        if (Objects.isNull(draft.getText()) || Objects.isNull(draft.getQuestionId()) || (HtmlUtils.getLength(draft.getText()) < 50 && isSubmit)) return false;
        Answer oldAnswer = null;
        draft.setAuthorId(uid);
        List<String> filenames = ImagesUtils.getFilenameFromHTML(draft.getText());
        draft.setText(HtmlUtils.modifyImgWidth(draft.getText(), 610));
        if (draft.getRelatedAnswer() != 0) {
            oldAnswer = answerDao.selectAnswerByIdAndUid(draft.getRelatedAnswer(), uid);
            if (Objects.isNull(oldAnswer)) draft.setRelatedAnswer(0);
        }
        if (draft.getRelatedAnswer() == 0) {
            if (!filenames.isEmpty()) imageService.addReferenceCountByFilenames(filenames);
        }
        else {
            Set<String> set = new HashSet<>(ImagesUtils.getFilenameFromHTML(oldAnswer.getText()));
            List<String> newFilenames = filenames.stream().filter(filename -> !set.contains(filename)).toList();
            if (!newFilenames.isEmpty()) imageService.addReferenceCountByFilenames(newFilenames);
        }
        if (isSubmit && pendingAnswerDao.addPendingAnswerAndSubmit(draft) != 1) return false;
        if (!isSubmit && pendingAnswerDao.addPendingAnswer(draft) != 1) return false;
        return true;
    }

    @Override
    public boolean updateDraft(PendingAnswer draft, int uid) {
        if (Objects.isNull(draft.getText()) || Objects.isNull(draft.getId()) || (!draft.getReviewed() && HtmlUtils.getLength(draft.getText()) < 50)) return false;
        Answer oldAnswer = pendingAnswerDao.getByIdAndUid(draft.getId(), uid);
        if (Objects.isNull(oldAnswer)) return false;
        List<String> oldImageFilenames = ImagesUtils.getFilenameFromHTML(oldAnswer.getText());
        List<String> newImageFilenames = ImagesUtils.getFilenameFromHTML(draft.getText());
        List<String> pendingAddImages = getDiffFilenames(oldImageFilenames, newImageFilenames);
        List<String> pendingRemoveImages = getDiffFilenames(newImageFilenames, oldImageFilenames);
        if (pendingAnswerDao.updateTextById(draft.getText(), draft.getId()) != 1) return false;
        imageService.addReferenceCountByFilenames(pendingAddImages);
        imageService.reduceReferenceCountByFilenames(pendingRemoveImages);
        return true;
    }

    @Override
    public boolean submitDraft(PendingAnswer draft, int uid) {
        if (draft.getText().length() < 50 || !updateDraft(draft, uid)) return false;
        pendingAnswerDao.letAnswerUnreviewed(draft.getId());
        return true;
    }

    @Override
    public boolean deleteDraftByIdAndUid(int id, int uid) {
        Answer answer = pendingAnswerDao.getByIdAndUid(id, uid);
        if (Objects.isNull(answer)) return false;
        List<String> filenames = ImagesUtils.getFilenameFromHTML(answer.getText());
        if (pendingAnswerDao.deleteByIdAndUid(id, uid) != 1) return false;
        imageService.reduceReferenceCountByFilenames(filenames);
        return true;
    }

    @Override
    public List<PendingAnswerWithQuestionTitle> getDraftsByUid(int uid, int page) {
        int start = page * 12;
        return pendingAnswerDao.selectByUid(uid, start);
    }

    /**
     * <p>该方法用来转换有效图片和清除无效图片</p>
     * <p>有效图片: Answer实体{@code data.getAnswer()}的text中携带的图片</p>
     * <p>无效图片: 在全部图片列表{@code data.getImagePaths()}中但不在Answer实体的text中的图片</p>
     * @param data
     * 带有一个Answer实体和一个JSON序列化后的包含全部图片的列表的实体
     * @throws JsonProcessingException
     * 如果{@code data}携带的列表反序列化失败
     */
    @Deprecated
    private void handlePendingAnswerData(PendingAnswerData data) throws JsonProcessingException {
        List<String> imageFilenames = ImagesUtils.getFilenameFromHTML(data.getAnswer().getText());
        if (data.getImagePaths() != null && !data.getImagePaths().equals("[]")) {
            List<String> pendingDeleteFilename;
            Set<String> pendingSaveImageFilenames = new HashSet<>(imageFilenames);
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> allImageFilenames =
                    objectMapper.readValue(data.getImagePaths(), new TypeReference<List<String>>() {})
                            .stream()
                            .map(ImagesUtils::getFilenameFromPath)
                            .toList();
            pendingDeleteFilename = allImageFilenames.stream().filter(name -> !pendingSaveImageFilenames.contains(name)).toList();
            Optional.of(pendingDeleteFilename).ifPresent(imageService::reduceReferenceCountByFilenames);
        }
        imageService.addReferenceCountByFilenames(imageFilenames);
    }

    /**
     * 获取{@code filenames2}中不同于{@code filenames1}的元素
     */
    private List<String> getDiffFilenames(List<String> filenames1, List<String> filenames2) {
        if (filenames1.isEmpty() || filenames2.isEmpty())
            return filenames2;
        Set<String> set = new HashSet<>(filenames1);
        return filenames2.stream()
                .filter(item -> !set.contains(item))
                .toList();
    }
}
