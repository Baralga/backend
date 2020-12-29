import { Turbo } from "@hotwired/turbo"
import { Application } from "stimulus"
import HelloController from "./controllers/hello_controller"
import TrackerController from "./controllers/tracker_controller"
import ActivityNewController from "./controllers/activity-new_controller"

const application = Application.start();

// register controllers
application.register("hello", HelloController);
application.register("tracker", TrackerController);
application.register("activity-new", ActivityNewController);