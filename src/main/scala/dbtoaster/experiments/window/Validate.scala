package dbtoaster.experiments.window

import scala.collection.mutable
import scala.io.Source

object Validate {
    def outputLineToTuple(line: String): (Int, Int, Int, Int) = {
        // pattern: (src, via1, via2, dst) -> 1
        val end = line.indexOf(")")
        lineToTuple(line.substring(1, end))
    }

    def lineToTuple(line: String): (Int, Int, Int, Int) = {
        // pattern: (src, via1, via2, dst) -> 1
        val arr = line.split(",")
        (Integer.valueOf(arr(0)), Integer.valueOf(arr(1)), Integer.valueOf(arr(2)), Integer.valueOf(arr(3)))
    }

    def getNextWindow(it: Iterator[String]): (Boolean, List[(Int, Int, Int, Int)], Iterator[String]) = {
        if (it.hasNext) {
            val isValid = (l: String) => l.startsWith("(")
            // loop until the first line that starts with "(" is found
            val (_, r) = it.span(line => !isValid(line))
            // retrieve the consecutive lines that starts with "("
            val (result, remain) = r.span(isValid)
            (true, result.toList.map(outputLineToTuple), remain)
        } else {
            (false, List(), null)
        }
    }

    def buildValidSnapshots(input: List[(Int, Int, Int, Int)]): Set[Set[(Int, Int, Int, Int)]] = {
        val graph = mutable.HashMap.empty[Int, mutable.HashSet[Int]]
        val result = mutable.HashSet.empty[Set[(Int, Int, Int, Int)]]

        //order by eventId
        for (event <- input.sortBy(_._1)) {
            if (event._2 == 1)
                graph.getOrElseUpdate(event._3, new mutable.HashSet[Int]()).add(event._4)
            else
                graph(event._3).remove(event._4)

            // compute all the paths with length = 3
            val snapshot = for {
                src <- graph.keySet
                via1 <- graph(src)
                via2 <- graph.getOrElse(via1, Set.empty[Int])
                dst <- graph.getOrElse(via2, Set.empty[Int])
            } yield (src, via1, via2, dst)

            result.add(snapshot.toSet)
        }

        result.toSet
    }

    def main(args: Array[String]): Unit = {
        val events = Source.fromResource("window/graph.csv").getLines().toList
        val snapshots = buildValidSnapshots(events.map(lineToTuple))

        val lines = Source.fromResource("window/output.log").getLines()
        var (success, paths, it) = getNextWindow(lines)

        while (success) {
            paths.foreach(println)
            println()
            assert(snapshots contains paths.toSet)

            val (success2, paths2, it2) = getNextWindow(it)
            success = success2
            paths = paths2
            it = it2
        }
    }
}
