import instance from "@/utils/request.js";
import qs from "qs";

export async function getPendingAnswersByPage(page) {
    return await instance.get("/pendingAnswers/getPendingAnswersByUid?page=" + page);
}

export async function updatePendingAnswers(id, status, text, type) {
    return await instance.put("/pendingAnswers/updateAnswer", qs.stringify({
        id: id,
        text: text,
        status: status,
        type: type
    }), {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    });
}

export async function deletePendingAnswersById(id) {
    return await instance.delete(`/pendingAnswers/deletePendingAnswerById?id=${id}`);
}

export async function getPendingQuestionsByPage(page) {
    return await instance.get("/pendingQuestions/getPendingQuestionsByUid?page=" + page);
}

export async function updatePendingQuestion(id, title, text, type) {
    return await instance.put("/pendingQuestions/updateQuestion", qs.stringify({
        id: id,
        title: title,
        text: text,
        type: type
    }), {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    });
}

export async function deletePendingQuestionsById(id) {
    return await instance.delete(`/pendingQuestions/deletePendingQuestionsById?id=${id}`);
}