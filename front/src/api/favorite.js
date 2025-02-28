import instance from "@/utils/request.js";
import qs from "qs";

export async function getFavoritesFolders(uid, page) {
    return await instance.get('/favorites/getFolders?uid=' + uid + '&page=' + page)
}

export async function addFavoritesFolders(name) {
    return await instance.post(`/favorites/createFavoritesFolder?name=${name}` )
}

export async function getFavorites(fid, page) {
    return await instance.get('/favorites/getFavorites?fid=' + fid + '&page=' + page)
}

export async function getPersonalFavoritesFolders(page, aid) {
    return await instance.get('/favorites/getPersonalFolders?page=' + page + '&aid=' + aid)
}

export async function checkFavored(aid) {
    return await instance.get('/favorites/checkFavored?aid=' + aid)
}

export async function doFavor(aid, fids, isFavor) {
    return await instance.post(
        '/favorites/doFavor',
        qs.stringify({
            fids: fids,
            aid: aid,
            isFavor: isFavor
        }, { arrayFormat: 'repeat' }),
        {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }
    );
}

export async function getFavoritesFolderInfo(fid) {
    let response = await instance.get(`/favorites//getFavoritesFolderInfo?fid=${fid}`);
    if (response.data && response.data.icon === null)
        response.data.icon = '/api/sys_images/default.webp'
    return response;
}

export async function deleteFavoritesFolder(fid) {
    return await instance.delete(`/favorites/deleteFavoritesFolder?fid=${fid}`);
}



