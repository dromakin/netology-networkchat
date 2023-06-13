/*
 * File:     ChatQueue
 * Package:  org.dromakin
 * Project:  networkchat
 *
 * Created by dromakin as 07.06.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.06.07
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package org.dromakin;

import java.util.concurrent.PriorityBlockingQueue;

public class ChatQueue {

    private volatile PriorityBlockingQueue<Integer> queue;

    private static ChatQueue instance;

    private ChatQueue() {
        queue = new PriorityBlockingQueue<>();
    }

    public static synchronized ChatQueue getInstance() {
        ChatQueue result = instance;
        if (result != null) {
            return result;
        }

        synchronized (ChatQueue.class) {
            if (instance == null) {
                instance = new ChatQueue();
            }
            return instance;
        }
    }

}
