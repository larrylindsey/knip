package org.knime.knip.core.ops.img;

import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.iterable.Mean;
import net.imglib2.ops.iterable.Variance;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.roi.PolygonRegionOfInterest;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

//TODO: Use circle instead of rectangle??
//TODO: Input: RandomAccessibleInterval Output: IterableInterval
public class MaxHomogenityOp<T extends RealType<T>> implements
                UnaryOperation<Img<T>, Img<T>> {

        private final long[] m_span;
        private final double m_lambda;
        private final OutOfBoundsFactory<T, Img<T>> m_outofbounds;

        public MaxHomogenityOp(double lambda, long[] span,
                        OutOfBoundsFactory<T, Img<T>> outofbounds) {
                m_span = span;
                m_lambda = lambda;
                m_outofbounds = outofbounds;

        }

        @Override
        public Img<T> compute(Img<T> input, Img<T> output) {

                PolygonRegionOfInterest[] rois = createROIs(input
                                .firstElement().createVariable(), m_span);

                double[] displacement = new double[input.numDimensions()];
                double[] position = new double[input.numDimensions()];

                Cursor<T> cursor = input.cursor();
                Cursor<T> outCursor = output.cursor();
                while (cursor.hasNext()) {
                        cursor.fwd();
                        outCursor.fwd();
                        cursor.localize(position);

                        double[] means = new double[rois.length];
                        double[] stddevs = new double[rois.length];
                        double minStdDev = Double.MAX_VALUE;

                        for (int d = 0; d < displacement.length; d++) {
                                displacement[d] = position[d] - displacement[d];
                        }

                        // Can be done more nicely? dont know
                        int r = 0;
                        for (PolygonRegionOfInterest roi : rois) {
                                roi.move(displacement);
                                // CODE START
                                Cursor<T> roiCursor = roi
                                                .getIterableIntervalOverROI(
                                                                Views.extend(input,
                                                                                m_outofbounds))
                                                .cursor();

                                means[r] = new Mean<T, DoubleType>().compute(
                                                roiCursor, new DoubleType())
                                                .getRealDouble();

                                roiCursor.reset();
                                stddevs[r] = Math
                                                .sqrt(new Variance<T, DoubleType>()
                                                                .compute(roiCursor,
                                                                                new DoubleType())
                                                                .getRealDouble());

                                minStdDev = Math.min(stddevs[r], minStdDev);

                                r++;
                                // CODE END
                        }

                        // Calc
                        double sum = 0;
                        double sum2 = 0;
                        for (int d = 0; d < stddevs.length; d++) {
                                stddevs[d] = minStdDev / stddevs[d];

                                if (Double.isNaN(stddevs[d]))
                                        stddevs[d] = 1;

                                double tmp = Math.pow(stddevs[d], m_lambda);
                                sum += tmp;
                                sum2 += tmp * means[d];
                        }

                        for (int d = 0; d < displacement.length; d++) {
                                displacement[d] = position[d];
                        }

                        outCursor.get().setReal(sum2 / sum);

                }

                return output;
        }

        private PolygonRegionOfInterest[] createROIs(T empty, long[] span) {

                // TODO: Only 2d case implemented and this is not well done can
                // be
                // automatized (either line or bresenham change ... can be
                // calculated
                // for n-dimensions) (nd)
                int numRois = 8;

                PolygonRegionOfInterest[] rois = new PolygonRegionOfInterest[numRois];
                int t = 0;

                Point origin = new Point(new long[span.length]);

                // T0
                rois[t] = new PolygonRegionOfInterest();
                rois[t].addVertex(0, origin);

                rois[t].addVertex(1, new Point(new long[] { span[0], 0 }));

                rois[t].addVertex(2, new Point(new long[] { span[0], span[1] }));
                t++;

                // T1
                rois[t] = new PolygonRegionOfInterest();
                rois[t].addVertex(0, origin);

                rois[t].addVertex(1, new Point(new long[] { span[0], 0 }));

                rois[t].addVertex(2,
                                new Point(new long[] { span[0], -span[1] }));
                t++;

                // T2
                rois[t] = new PolygonRegionOfInterest();
                rois[t].addVertex(0, origin);

                rois[t].addVertex(1,
                                new Point(new long[] { span[0], -span[1] }));
                rois[t].addVertex(2, new Point(new long[] { 0, -span[1] }));

                t++;

                // T3
                rois[t] = new PolygonRegionOfInterest();
                rois[t].addVertex(0, origin);

                rois[t].addVertex(1, new Point(new long[] { 0, -span[1] }));
                rois[t].addVertex(2, new Point(
                                new long[] { -span[0], -span[1] }));

                t++;

                // T4
                rois[t] = new PolygonRegionOfInterest();
                rois[t].addVertex(0, origin);

                rois[t].addVertex(1, new Point(new long[] { -span[0], 0 }));
                rois[t].addVertex(2, new Point(
                                new long[] { -span[0], -span[1] }));

                t++;

                // T5
                rois[t] = new PolygonRegionOfInterest();
                rois[t].addVertex(0, origin);

                rois[t].addVertex(1, new Point(new long[] { -span[0], 0 }));
                rois[t].addVertex(2,
                                new Point(new long[] { -span[0], span[1] }));

                t++;

                // T6
                rois[t] = new PolygonRegionOfInterest();
                rois[t].addVertex(0, origin);

                rois[t].addVertex(1, new Point(new long[] { 0, span[1] }));
                rois[t].addVertex(2,
                                new Point(new long[] { -span[0], span[1] }));

                t++;

                // T7
                rois[t] = new PolygonRegionOfInterest();
                rois[t].addVertex(0, origin);

                rois[t].addVertex(1, new Point(new long[] { 0, span[1] }));
                rois[t].addVertex(2, new Point(new long[] { span[0], span[1] }));

                t++;

                return rois;
        }

        @Override
        public UnaryOperation<Img<T>, Img<T>> copy() {
                return new MaxHomogenityOp<T>(m_lambda, m_span.clone(),
                                m_outofbounds);
        }

}