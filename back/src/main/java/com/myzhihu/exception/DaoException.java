package com.myzhihu.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DaoException extends RuntimeException {

    String msg;

}
