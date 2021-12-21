package dbtoaster.experiments.arbitrary

import dbtoaster.experiments.util.Validator

object ValidateArbitrary {
    def main(args: Array[String]): Unit = {
        Validator.validate("arbitrary/graph.csv", "arbitrary/output.log")
    }
}
