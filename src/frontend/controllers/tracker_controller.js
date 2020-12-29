import { Controller } from "stimulus"
import dayjs from "dayjs"

export default class extends Controller {
        static get targets() {
          return [ "name", "controlStart", "controlStop", "controlSwitchProject", "start", "duration" ]
        }

        updateTimer() {
            let endTime = dayjs();

            let hours = endTime.diff(this.startTime, "h")
            let minutes = endTime.diff(this.startTime, "m")

            this.durationTarget.innerHTML = hours.toString().padStart(2, "0") + ":" + minutes.toString().padStart(2, "0");

            fetch("/activities/ping");
        }

        start() {
            this.startTime = dayjs();

            this.startTarget.innerHTML = this.startTime.format("HH:mm");

            this.controlStartTarget.style.display = "none";
            this.controlStopTarget.style.display = "inline-flex";
            this.controlSwitchProjectTarget.disabled = true;

            this.updateTimer();
            this.updateTimerFunction = setInterval(() => {
              this.updateTimer()
            }, 10000);
        }

        stop() {
            this.endTime = dayjs();

            this.startTarget.innerHTML = "";
            this.durationTarget.innerHTML = "";

            if (this.updateTimerFunction) {
                clearInterval(this.updateTimerFunction);
            }

            this.element.querySelector("input[name='day']").value = this.startTime.format("DD/MM/YYYY");
            this.element.querySelector("input[name='startTime']").value = this.startTime.format("HH:mm");
            this.element.querySelector("input[name='endTime']").value = this.endTime.format("HH:mm");

            this.controlStartTarget.style.display = "inline-flex";
            this.controlStopTarget.style.display = "none";
            this.controlSwitchProjectTarget.disabled = false;

            this.element.requestSubmit();
        }
}