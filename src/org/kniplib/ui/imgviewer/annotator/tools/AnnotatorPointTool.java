package org.kniplib.ui.imgviewer.annotator.tools;

import org.kniplib.ui.imgviewer.events.ImgViewerMouseEvent;
import org.kniplib.ui.imgviewer.events.PlaneSelectionEvent;
import org.kniplib.ui.imgviewer.overlay.Overlay;
import org.kniplib.ui.imgviewer.overlay.OverlayElementStatus;
import org.kniplib.ui.imgviewer.overlay.elements.PointOverlayElement;

public class AnnotatorPointTool extends
                AnnotationDrawingTool<PointOverlayElement<String>> {

        public AnnotatorPointTool() {
                super("Point", "tool-point.png");
        }

        @Override
        public void onMouseDoubleClickLeft(ImgViewerMouseEvent e,
                        PointOverlayElement<String> currentOverlayElement,
                        PlaneSelectionEvent selection, Overlay<String> overlay,
                        String... labels) {
                // Nothing to do here
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onMousePressedLeft(ImgViewerMouseEvent e,
                        PointOverlayElement<String> currentOverlayElement,
                        PlaneSelectionEvent selection, Overlay<String> overlay,
                        String... labels) {
                PointOverlayElement<String> element = new PointOverlayElement<String>(
                                e.getPosX(),
                                e.getPosY(),
                                selection.getPlanePos(e.getPosX(), e.getPosY()),
                                selection.getDimIndices(), labels);

                overlay.addElement(element);

                if (setCurrentOverlayElement(OverlayElementStatus.ACTIVE,
                                element)) {
                        fireStateChanged();
                }
        }

        @Override
        public void onMouseReleasedLeft(ImgViewerMouseEvent e,
                        PointOverlayElement<String> currentOverlayElement,
                        PlaneSelectionEvent selection, Overlay<String> overlay,
                        String... labels) {
                // Nothing to do here
        }

        @Override
        public void onMouseDraggedLeft(ImgViewerMouseEvent e,
                        PointOverlayElement<String> currentOverlayElement,
                        PlaneSelectionEvent selection, Overlay<String> overlay,
                        String... labels) {
                // Nothing to do here
        }
}
