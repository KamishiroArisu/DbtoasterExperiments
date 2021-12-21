package dbtoaster.experiments.window

import dbtoaster.experiments.util.Validator

object ValidateWindow {
    def main(args: Array[String]): Unit = {
        Validator.validate("window/graph.csv", "window/output.log")
    }
}
