package org.knime.knip.core.ui.imgviewer.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;

import org.knime.knip.core.awt.ImageRenderer;
import org.knime.knip.core.awt.RendererFactory;
import org.knime.knip.core.ui.event.EventListener;
import org.knime.knip.core.ui.event.EventService;
import org.knime.knip.core.ui.imgviewer.ViewerComponent;
import org.knime.knip.core.ui.imgviewer.events.ImgRedrawEvent;
import org.knime.knip.core.ui.imgviewer.events.IntervalWithMetadataChgEvent;
import org.knime.knip.core.ui.imgviewer.events.RendererSelectionChgEvent;

/**
 * Allows the user to select a certain renderer.
 *
 * Publishes {@link RendererSelectionChgEvent}
 *
 * @author dietzc, hornm, fschoenenberger
 */
public class RendererSelectionPanel<T extends Type<T>, I extends RandomAccessibleInterval<T>>
                extends ViewerComponent {

        private static final long serialVersionUID = 1L;

        private JList m_rendList;

        private EventService m_eventService;

        private boolean m_blockEvent = false;

        public RendererSelectionPanel() {

                super("Renderering", false);

                // renderer selection
                setPreferredSize(new Dimension(200, getMinimumSize().height));
                setMaximumSize(new Dimension(250, getMaximumSize().height));
                setMinimumSize(new Dimension(100, getMinimumSize().height));
                setLayout(new BorderLayout());

                m_rendList = new JList();
                m_rendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                m_rendList.setSelectedIndex(0);

                m_rendList.addListSelectionListener(new ListSelectionListener() {

                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                                if (e.getValueIsAdjusting() || m_blockEvent)
                                        return;
                                else {
                                        m_eventService.publish(new RendererSelectionChgEvent(
                                                        (ImageRenderer) m_rendList
                                                                        .getSelectedValue()));
                                        m_eventService.publish(new ImgRedrawEvent());
                                }

                        }
                });

                add(new JScrollPane(m_rendList), BorderLayout.CENTER);
        }

        /**
         * @param axes
         * @param name
         */
        @EventListener
        public void onImgUpdated(IntervalWithMetadataChgEvent<I> e) {
                // if (m_imgRenderers.isEmpty()) {

                ImageRenderer<T, I>[] tmp = RendererFactory
                                .createSuitableRenderer(e.getInterval());
                m_blockEvent = true;
                m_rendList.setListData(tmp);
                // m_rendList.setSelectedIndex(0);
                m_rendList.repaint();
                m_blockEvent = false;

                // }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Position getPosition() {
                return Position.SOUTH;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setEventService(EventService eventService) {
                m_eventService = eventService;
                eventService.subscribe(this);
        }

        @Override
        public void saveComponentConfiguration(ObjectOutput out)
                        throws IOException {
                out.writeInt(m_rendList.getSelectedIndex());
        }

        @Override
        public void loadComponentConfiguration(ObjectInput in)
                        throws IOException, ClassNotFoundException {
                m_rendList.setSelectedIndex(in.readInt());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void reset() {
        }

        @Override
        public void setParent(Component parent) {
                // Nothing to do here
        }

}