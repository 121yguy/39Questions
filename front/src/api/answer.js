import instance from "@/utils/request.js";
import qs from "qs";

export async function getAnswersByQuestionId(questionId, startId) {
    return await instance.get(`/answers/getAllByQuestionId/${questionId}/${startId}`);
}

export async function getAnswerById(aid) {
    return await instance.get('/answers/getAnswerById/' + aid);
}

export async function getAnswersByUserId(uid, startId) {
    return await instance.get(`/answers/getAnswersByUserId/${uid}/${startId}`);
}

export async function like(aid) {
    return await instance.post('/likes/like?aid=' + aid);
}

export async function cancelLike(aid) {
    return await instance.delete('/likes/cancelLike?aid=' + aid);
}

export async function deleteAnswer(aid) {
    return await instance.delete('/answers/deleteByAid/' + aid);
}

export async function editAnswer(aid, text) {
    return await instance.put('/answers/updateByAid', qs.stringify({
        text: text,
        aid: aid
    }), {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    });
}

export async function addPendingAnswer(questionId, text, relativeAnswer) {
    return await instance.post('/pendingAnswers/addAnswer', JSON.stringify({
        questionId: questionId,
        text: text,
        relatedAnswer: relativeAnswer
    }), {
        headers: {
            'content-type': 'application/json',
        }
    })
}

export async function addUnsubmittedPendingAnswer(questionId, text, relativeAnswer) {
    return await instance.post('/pendingAnswers/addUnsubmittedAnswer', JSON.stringify({
        questionId: questionId,
        text: text,
        relatedAnswer: relativeAnswer
    }), {
        headers: {
            'content-type': 'application/json',
        }
    })
}

export async function getQuestionAndAnswer(aid, qid) {
    return await instance.get(`/answers/getQuestionAndAnswer?aid=${aid}&qid=${qid}`);
}