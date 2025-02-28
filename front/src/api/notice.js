import instance from "@/utils/request.js";

export async function getNumbersOfUnreadNotices() {
    return await instance.get('/notices/getNumbersOfUnreadNotices');
}

export async function getNotices(page) {
    return await instance.get(`/notices/getNotices?page=${page}`);
}

export async function updateOperateTime(id) {
    return await instance.put(`/notices/addReadTime?id=${id}`);
}

export async function readAllNotices() {
    return await instance.put('/notices/readAllNotices');
}