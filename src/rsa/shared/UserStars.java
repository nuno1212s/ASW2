package rsa.shared;

public enum UserStars {

    FIVE_STARS {
        @Override
        public int getStars() {
            return 5;
        }
    },
    FOUR_STARS {
        @Override
        public int getStars() {
            return 4;
        }
    },
    ONE_STAR {
        @Override
        public int getStars() {
            return 1;
        }
    },
    THREE_STARS {
        @Override
        public int getStars() {
            return 3;
        }
    },
    TWO_STARS {
        @Override
        public int getStars() {
            return 2;
        }
    };

    public abstract int getStars();

}
