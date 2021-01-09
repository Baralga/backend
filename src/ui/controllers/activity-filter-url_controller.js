import { Controller } from "stimulus"
import dayjs from "dayjs"

export default class extends Controller {

    static values = { params: String }

    connect() {
        history.replaceState(null, null, "/" + this.paramsValue);
    }
}