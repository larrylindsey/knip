package org.knime.knip.core.ui.imgviewer.panels.infobars;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.meta.CalibratedSpace;
import net.imglib2.type.Type;

/**
 * @author dietzc
 */
public class ImgViewInfoPanel<T extends Type<T>> extends ViewInfoPanel<T> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /** Updates cursor probe label. */
    @Override
    protected String updateMouseLabel(final StringBuffer buffer, final Interval interval, final CalibratedSpace axes,
                                      final RandomAccess<T> rndAccess, final long[] coords) {

        if (interval == null) {
            return "";
        }
        if (m_sel == null) {
            return "No plane selected";
        }

        buffer.setLength(0);

        for (int i = 0; i < coords.length; i++) {
            buffer.append(" ");
            if (i < interval.numDimensions()) {
                buffer.append(axes != null ? axes.axis(i).getLabel() : i);
            }
            if (coords[i] == -1) {
                buffer.append("[ Not set ];");
            } else {
                buffer.append("[" + (coords[i] + 1) + "/" + interval.dimension(i) + "];");
            }
        }
        if (buffer.length() > 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        String val;

        if ((coords[m_sel.getPlaneDimIndex1()] != -1) && (coords[m_sel.getPlaneDimIndex2()] != -1)) {
            rndAccess.setPosition(coords);
            val = rndAccess.get().toString();
        } else {
            val = "Not set";
        }

        buffer.append("; value=");
        buffer.append(val);

        return buffer.toString();

    }

    @Override
    protected String updateImageLabel(final StringBuffer buffer, final Interval interval,
                                      final RandomAccess<T> rndAccess, final String imgName) {

        if (interval == null) {
            return "No image set";
        }

        buffer.setLength(0);

        if ((imgName != null) && (imgName.length() > 0)) {
            buffer.append(imgName + "; ");
        }

        buffer.append("type=");
        buffer.append(rndAccess.get().getClass().getSimpleName());

        return buffer.toString();

    }

    @Override
    public void saveComponentConfiguration(final ObjectOutput out) throws IOException {
        // Nothing to do here
    }

    @Override
    public void loadComponentConfiguration(final ObjectInput in) throws IOException {
        // Nothing to do here
    }

}