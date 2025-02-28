package com.myzhihu.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServiceException extends RuntimeException {
    String msg;
}
