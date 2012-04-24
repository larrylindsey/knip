package org.kniplib.tools;

import net.imglib2.Interval;

public class IntervalUtils {

        public static synchronized boolean intervalEquals(Interval a, Interval b) {

                if (a.numDimensions() != b.numDimensions()) {
                        return false;
                }

                for (int d = 0; d < a.numDimensions(); d++) {
                        if (a.min(d) != b.min(d) || a.max(d) != b.max(d))
                                return false;
                }

                return true;
        }
}
