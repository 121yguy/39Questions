import instance from "@/utils/request.js";
export async function getQuestionsByPage(page) {
    return await instance.get("/questions/getByPage2/" + page)
}
export async function getQuestionById(id) {
    return await instance.get('/questions/getById/' + id)
}
export async function getUserQuestionsByPage2(startId, uid) {
    return await instance.get('/questions/getUserQuestionByPage?startId=' + startId + '&uid=' + uid)
}

export async function getQuestionsWithoutAnswer(headId) {
    return await instance.get('/questions/getQuestionsWithoutAnswer?headId=' + headId)
}

export async function searchQuestions(keywords, page) {
    return await instance.get('/questions/search?page=' + page + '&keywords=' + encodeURIComponent(keywords))
}

export async function addPendingQuestion(title, text) {
    return await instance.post('/pendingQuestions/addQuestion', JSON.stringify({
        title: title,
        text: text,
    }), {
        headers: {
            'content-type': 'application/json',
        }
    })
}

export async function addUnsubmittedPendingQuestion(title, text) {
    return await instance.post('/pendingQuestions/addUnsubmittedQuestion', JSON.stringify({
        title: title,
        text: text,
    }), {
        headers: {
            'content-type': 'application/json',
        }
    })
}

export async function getHomePageQuestions(sessionId) {
    return await instance.get(`/questions/getHomePageQuestions?sessionId=${sessionId}`)
}

