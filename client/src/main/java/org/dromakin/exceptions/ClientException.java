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

public class ClientException extends Exception {

    public ClientException(String s) {
        super(s);
    }

    public ClientException(String s, Throwable throwable) {
        super(s, throwable);
    }

}
