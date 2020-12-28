import { Application } from "stimulus"
import HelloController from "./controllers/hello_controller"

const application = Application.start()

// register controllers
application.register("hello", HelloController)
