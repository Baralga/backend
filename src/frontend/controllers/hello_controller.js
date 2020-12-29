import { Controller } from "stimulus"
import dayjs from "dayjs"

export default class extends Controller {

        connect() {
            console.log("TEst", dayjs());
        }

}