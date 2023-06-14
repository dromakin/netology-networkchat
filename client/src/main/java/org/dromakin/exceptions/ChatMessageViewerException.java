/*
 * File:     ClientException
 * Package:  org.dromakin
 * Project:  netology_client_server
 *
 * Created by dromakin as 21.01.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.01.21
 * copyright - Echelon Inc. 2023
 */

package org.dromakin.exceptions;

public class ChatMessageViewerException extends Exception {

    public ChatMessageViewerException(String s) {
        super(s);
    }

    public ChatMessageViewerException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public void print() {
        System.err.println(getMessage());
        if (getCause() != null)
            getCause().printStackTrace();
    }
}
