import { Turbo } from "@hotwired/turbo"
import { Application } from "stimulus"
import TrackerController from "./controllers/tracker_controller"
import ActivityNewController from "./controllers/activity-new_controller"
import ActivityFilterUrlController from "./controllers/activity-filter-url_controller"
import NavbarController from "./controllers/navbar_controller"

import baralga_48px from './baralga_48px.png';

const application = Application.start();

// register controllers
application.register("tracker", TrackerController);
application.register("activity-new", ActivityNewController);
application.register("activity-filter-url", ActivityFilterUrlController);
application.register("navbar", NavbarController);