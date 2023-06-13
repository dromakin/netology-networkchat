/*
 * File:     ChatClients
 * Package:  org.dromakin
 * Project:  networkchat
 *
 * Created by dromakin as 08.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.08
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class ChatClients {

    @Getter(AccessLevel.PUBLIC)
    private volatile ConcurrentHashMap<String, ClientHandler> activeClients = new ConcurrentHashMap<>();


}
