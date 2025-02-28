import instance from "@/utils/request.js";
import qs from "qs";

export async function deleteImagesByPaths(pathsJson) {
    return await instance.delete('/pics/deleteImagesByPaths', {
        headers: {
            'Content-Type': 'application/json',
        },
        data: pathsJson
    });
}

export async function convertPendingImages(pathsJson) {
    return await instance.post('/pics/convertPendingImages', {
        headers: {
            'Content-Type': 'application/json',
        },
        data: pathsJson
    });
}