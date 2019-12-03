package models;

import lombok.Data;

@Data
    public class Players {
        public String user_ID;
        public String user_name;
        public String name;
        public String password;
        public int high_score;

        public Players(String user_ID, String username, String full_name, String password, int high_score) {
            this.user_ID = user_ID;
            this.user_name = username;
            this.name = full_name;
            this.password = password;
            this.high_score = high_score;
        }

    }
