/*
 * File:     ClientMessage
 * Package:  org.dromakin.models
 * Project:  networkchat
 *
 * Created by dromakin as 08.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.08
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter(AccessLevel.PUBLIC)
public class ClientMessage {
    private Long time;
    private String nickname;
    private String message;
}
