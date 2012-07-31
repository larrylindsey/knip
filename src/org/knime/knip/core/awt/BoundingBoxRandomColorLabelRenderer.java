package org.knime.knip.core.awt;

import java.awt.Graphics;
import java.util.Set;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.ScreenImage;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingMapping;
import net.imglib2.labeling.LabelingType;
import net.imglib2.type.Type;

import org.knime.knip.core.ui.imgviewer.events.RulebasedLabelFilter.Operator;

public class BoundingBoxRandomColorLabelRenderer<L extends Comparable<L> & Type<L>>
                extends BoundingBoxLabelRenderer<L> {

        private final RandomColorLabelingRenderer<L> m_labelRenderer;
        private RandomAccessibleInterval<LabelingType<L>> m_source;
        private int m_dimX;
        private int m_dimY;
        private long[] m_planePos;

        public BoundingBoxRandomColorLabelRenderer() {
                m_labelRenderer = new RandomColorLabelingRenderer<L>();
        }

        @Override
        public ScreenImage render(Labeling<L> source, int dimX, int dimY,
                        long[] planePos) {
                m_dimX = dimX;
                m_dimY = dimY;
                m_planePos = planePos;
                m_source = source;
                return super.render(source, dimX, dimY, planePos);
        }

        @Override
        protected ScreenImage createCanvas(int width, int height) {

                ScreenImage ret = new ARGBScreenImage(width, height);
                ScreenImage labelRendererResult = m_labelRenderer.render(
                                m_source,
                                m_dimX, m_dimY, m_planePos);
                Graphics g = ret.image().getGraphics();
                g.drawImage(labelRendererResult.image(), 0, 0, width, height,
                                null);

                return ret;
        }

        @Override
        public String toString() {
                return "Bounding Box Color Labeling Renderer";
        }

        @Override
        public void setHilitedLabels(Set<String> hilitedLabels) {
                super.setHilitedLabels(hilitedLabels);
                m_labelRenderer.setHilitedLabels(hilitedLabels);
        }

        @Override
        public void setActiveLabels(Set<String> activeLabels) {
                super.setActiveLabels(activeLabels);
                m_labelRenderer.setActiveLabels(activeLabels);
        }

        @Override
        public void setHiliteMode(boolean isHiliteMode) {
                super.setHiliteMode(isHiliteMode);
                m_labelRenderer.setHiliteMode(isHiliteMode);
        }

        @Override
        public void setLabelMapping(LabelingMapping<L> labelMapping) {
                super.setLabelMapping(labelMapping);
                m_labelRenderer.setLabelMapping(labelMapping);
        }

        @Override
        public void setOperator(Operator operator) {
                super.setOperator(operator);
                m_labelRenderer.setOperator(operator);
        }
}