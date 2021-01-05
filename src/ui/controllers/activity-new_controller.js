import { Controller } from "stimulus"
import dayjs from "dayjs"

export default class extends Controller {

        static get targets() {
          return [ "day", "startTime", "endTime" ]
        }

        connect() {
            let now = dayjs();

            this.dayTarget.value = now.format("DD/MM/YYYY");
            this.startTimeTarget.value = now.format("HH:mm");
            this.endTimeTarget.value = now.format("HH:mm");
        }
}