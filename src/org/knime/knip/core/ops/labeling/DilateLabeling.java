package org.knime.knip.core.ops.labeling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.view.Views;

import org.knime.knip.core.data.StructuringElementCursor;

/**
 * Dilate operation on Labeling.
 * 
 * @author schoenenf
 * 
 * @param <L>
 */
public class DilateLabeling<L extends Comparable<L>> implements
                UnaryOperation<Labeling<L>, Labeling<L>> {

        private long[][] sortStructuringElement(
                        final long[][] structuringElement) {
                final ArrayList<long[]> offsets = new ArrayList<long[]>();
                for (final long[] off : structuringElement) {
                        offsets.add(off);
                }
                Collections.sort(offsets, new Comparator<long[]>() {

                        @Override
                        public int compare(final long[] o1, final long[] o2) {
                                double dist1 = 0;
                                double dist2 = 0;
                                for (int i = 0; i < o1.length; i++) {
                                        dist1 += o1[i] * o1[i];
                                        dist2 += o2[i] * o2[i];
                                }
                                dist1 = Math.sqrt(dist1);
                                dist2 = Math.sqrt(dist2);
                                if (dist1 < dist2) {
                                        return -1;
                                }
                                if (dist1 > dist2) {
                                        return 1;
                                }
                                return 0;
                        }
                });
                final long[][] struc = new long[offsets.size()][];
                offsets.toArray(struc);
                return struc;
        }

        private final long[][] m_struc;

        private final boolean m_labelBased;

        public DilateLabeling(final long[][] structuringElement) {
                this(structuringElement, true);
        }

        /**
         * 
         * @param structuringElement
         * @param labelBased
         *                Label-based / binary-based switch.
         *                <ul>
         *                <li>Label-based: Each region defined by a label is
         *                dilated individually. If the label is present in one
         *                of the neighbor pixel it is also added to the center.</li>
         *                <li>Binary-based: The labeling is treated as a binary
         *                image. If one of the neighbor pixels is not empty the
         *                center pixel is set to the nearest labeling. No new
         *                label combination is composed.</li>
         *                </ul>
         */
        public DilateLabeling(final long[][] structuringElement,
                        final boolean labelBased) {
                m_struc = labelBased ? structuringElement
                                : sortStructuringElement(structuringElement);
                m_labelBased = labelBased;
        }

        @Override
        public Labeling<L> compute(final Labeling<L> input,
                        final Labeling<L> output) {
                if (m_labelBased) {
                        return computeLabelBased(input, output);
                } else {
                        return computeBinaryBased(input, output);
                }
        }

        private Labeling<L> computeLabelBased(final Labeling<L> input,
                        final Labeling<L> output) {
                final StructuringElementCursor<LabelingType<L>> outStructure = new StructuringElementCursor<LabelingType<L>>(
                                Views.extendValue(output, new LabelingType<L>())
                                                .randomAccess(), m_struc);
                for (final L label : input.getLabels()) {
                        final Cursor<LabelingType<L>> in = input
                                        .getIterableRegionOfInterest(label)
                                        .getIterableIntervalOverROI(input)
                                        .localizingCursor();
                        while (in.hasNext()) {
                                in.next();
                                outStructure.relocate(in);
                                while (outStructure.hasNext()) {
                                        outStructure.next();
                                        addLabel(outStructure.get(), label);
                                }
                        }
                }
                return output;
        }

        private Labeling<L> computeBinaryBased(final Labeling<L> input,
                        final Labeling<L> output) {
                final StructuringElementCursor<LabelingType<L>> inStructure = new StructuringElementCursor<LabelingType<L>>(
                                Views.extendValue(input, new LabelingType<L>())
                                                .randomAccess(), m_struc);
                final Cursor<LabelingType<L>> out = output.localizingCursor();
                next: while (out.hasNext()) {
                        out.next();
                        inStructure.relocate(out);
                        final List<L> center = inStructure.get().getLabeling();
                        if (!center.isEmpty()) {
                                continue next;
                        }
                        while (inStructure.hasNext()) {
                                inStructure.next();
                                if (!inStructure.get().getLabeling().isEmpty()) {
                                        out.get().set(inStructure.get());
                                        continue next;
                                }
                        }
                        out.get().setLabeling(
                                        out.get().getMapping().emptyList());
                }
                return output;
        }

        private void addLabel(final LabelingType<L> type, final L elmnt) {
                if (type.getLabeling().contains(elmnt)) {
                        return;
                }
                final List<L> current = type.getLabeling();
                final ArrayList<L> tmp = new ArrayList<L>();
                tmp.addAll(current);
                tmp.add(elmnt);
                type.setLabeling(tmp);
        }

        @Override
        public UnaryOperation<Labeling<L>, Labeling<L>> copy() {
                return new DilateLabeling<L>(m_struc);
        }
}