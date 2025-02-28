import instance from "@/utils/request.js";

export async function getList(type) {
    return await instance.post('/managements/getList?type=' + type);
}

export async function approve(id, type) {
    return await instance.post('/managements/approve?id=' + id + '&type=' + type);
}

export async function reject(id, type) {
    return await instance.post('/managements/reject?id=' + id + '&type=' + type);
}

export async function finishReview(type) {
    return await instance.post('/managements/finishReview?type=' + type);
}

export async function checkStatus(type) {
    return await instance.get('/managements/checkStatus?type=' + type);
}