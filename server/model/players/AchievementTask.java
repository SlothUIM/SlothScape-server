   package server.model.players;

   public class AchievementTask {
        private String task;
        private boolean done;

        public AchievementTask(String task, boolean done) {
            this.task = task;
            this.done = done;
        }

        public String getTask() {
            return task;
        }

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }
    }