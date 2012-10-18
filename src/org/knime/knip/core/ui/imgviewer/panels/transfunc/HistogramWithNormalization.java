/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003, 2010
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 */
package org.knime.knip.core.ui.imgviewer.panels.transfunc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A Histogram that knows what values to cut off for normalizing.<br>
 * 
 * @author muethingc
 * 
 */
public class HistogramWithNormalization implements Iterable<Integer> {

        private class Iter implements Iterator<Integer> {

                private final int end;
                private int pos;

                public Iter(final int s, final int e) {
                        pos = s;
                        end = e;
                }

                @Override
                public boolean hasNext() {
                        return pos < end;
                }

                @Override
                public Integer next() {
                        if (pos < end) {
                                return m_data[pos++];
                        } else {
                                throw new NoSuchElementException();
                        }
                }

                @Override
                public void remove() {
                        throw new UnsupportedOperationException();
                }
        }

        private final int[] m_data;

        /* The first and last occurence in m_data that is not null */
        /* with [0] = fist and [1] = last */
        private final int[] m_pos;

        /* same as above, but as a fraction of the lenght of m_data */
        private final double[] m_frac;

        /**
         * Set up a new instance with the passed data.<br>
         * 
         * @param data
         *                the data to use for this histogram, a deep copy will
         *                be made
         */
        public HistogramWithNormalization(final int[] data) {
                if (data == null)
                        throw new NullPointerException();
                m_data = data.clone();

                m_pos = findFirstLast(m_data);

                m_frac = calcFractions(m_data, m_pos);
        }

        private double[] calcFractions(final int[] data, final int[] pos) {
                assert data != null;
                assert pos != null;

                double[] frac = new double[2];

                frac[0] = (double) pos[0] / (double) data.length;
                frac[1] = (double) pos[1] / (double) data.length;

                return frac;
        }

        private int[] findFirstLast(final int[] data) {

                assert (data != null);

                int[] res = new int[2];

                // find the min Position
                for (int i = 0; i < data.length; i++) {
                        if (data[i] != 0) {
                                res[0] = i;
                                break;
                        }
                }

                // find the max Position
                for (int i = data.length - 1; i >= 0; i--) {
                        if (data[i] != 0) {
                                res[1] = i;
                                break;
                        }
                }

                return res;
        }

        /**
         * Get the first and last index where data[index] is not zero.
         * 
         * @return positions
         */
        public int[] getPos() {
                return m_pos.clone();
        }

        /**
         * Same as {@link getPos()}, but returning the positions as a fraction
         * of the lenght of the data array.<br>
         * 
         * @return fractions
         */
        public double[] getFractions() {
                return m_frac.clone();
        }

        /**
         * Get a copy of the data array.<br>
         * 
         * @return the date
         */
        public int[] getData() {
                return m_data.clone();
        }

        /**
         * Get a copy of the part of the array that correspondes to the
         * normalized part of the histogram.<br>
         * 
         * @return the normalized part of the data
         */
        public int[] getNormalizedData() {
                return Arrays.copyOfRange(m_data, m_pos[0], m_pos[1]);
        }

        /**
         * Get the value of the histogram at the given index.<br>
         * 
         * @param index
         * 
         * @return value
         */
        public int get(final int index) {
                return m_data[index];
        }

        /**
         * Get an iterator over the complete data set.<br>
         * 
         * @return iterator
         */
        public Iterator<Integer> iteratorFull() {
                return new Iter(0, m_data.length);
        }

        /**
         * Get an iterator that only iterates over the values that are within
         * the normalization range.<br>
         * 
         * @return iterator
         */
        public Iterator<Integer> iteratorNormalized() {
                return new Iter(m_pos[0], m_pos[1] + 1);
        }

        @Override
        public Iterator<Integer> iterator() {
                return iteratorFull();
        }

        /**
         * Get the size of the underlying data array.<br>
         */
        public int size() {
                return m_data.length;
        }
}