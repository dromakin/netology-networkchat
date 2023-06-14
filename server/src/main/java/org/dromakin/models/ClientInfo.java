/*
 * File:     ClientInfo
 * Package:  org.dromakin.models
 * Project:  networkchat
 *
 * Created by dromakin as 07.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.07
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin.models;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter(AccessLevel.PUBLIC)
public class ClientInfo {
    String nickname;
    String name;
    String surname;
}
