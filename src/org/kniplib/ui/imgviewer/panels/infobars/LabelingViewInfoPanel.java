package org.kniplib.ui.imgviewer.panels.infobars;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.imglib2.RandomAccess;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.meta.CalibratedSpace;

/**
 * 
 * 
 * 
 * @author dietzc, hornm, schoenenbergerf
 */
public class LabelingViewInfoPanel<L extends Comparable<L>> extends
                ViewInfoPanel<LabelingType<L>, Labeling<L>> {

        /**
	 * 
	 */
        private static final long serialVersionUID = 1L;

        /** Updates cursor probe label. */
        @Override
        protected String updateLabel(Labeling<L> img, StringBuffer buffer,
                        CalibratedSpace axes,
                        RandomAccess<LabelingType<L>> rndAccess, long[] coords) {
                if (img == null)
                        return "No image set";
                if (m_sel == null)
                        return "No plane selected";

                buffer.setLength(0);

                for (int i = 0; i < coords.length; i++) {
                        buffer.append(" ");
                        if (i < img.numDimensions()) {
                                buffer.append(axes != null ? axes.axis(i)
                                                .getLabel() : i);
                        }
                        if (coords[i] == -1) {
                                buffer.append("[ Not set ];");
                        } else {
                                buffer.append("[" + (coords[i] + 1) + "/"
                                                + img.dimension(i) + "];");
                        }
                }
                if (buffer.length() > 0) {
                        buffer.deleteCharAt(buffer.length() - 1);
                }
                String val;

                if (coords[m_sel.getPlaneDimIndex1()] != -1
                                && coords[m_sel.getPlaneDimIndex2()] != -1) {
                        rndAccess.setPosition(coords);
                        val = "[";
                        if (rndAccess.get().getLabeling().size() > 0) {
                                for (L label : rndAccess.get().getLabeling()) {
                                        val += label.toString() + ";";
                                }
                                val = val.substring(0, val.length() - 1);
                                val += "]";
                        } else {
                                val += "EmptyLabel]";
                        }

                } else {
                        val = "Not set";
                }

                buffer.append("; value=");
                buffer.append(val);
                buffer.append("; type=");
                buffer.append(rndAccess.get().getClass().getSimpleName());

                return buffer.toString();
        }

        public void onLabelingChanged() {

        }

        @Override
        public void saveComponentConfiguration(ObjectOutput out)
                        throws IOException {
                // Nothing to do here
        }

        @Override
        public void loadComponentConfiguration(ObjectInput in)
                        throws IOException {
                // Nothing to do here
        }

}
