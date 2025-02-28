import instance from "@/utils/request.js";
import qs from "qs";

export async function getCommentsByStartId(startId) {
    return await instance.get('/comments/getComments?startId=' + startId);
}

export async function addComment(content) {
    return await instance.post('/comments/addComment', qs.stringify({
        content: content,
    }), {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    });
}

export async function deleteCommentById(id) {
    return await instance.delete(`/comments/deleteComment?id=${id}`);
}