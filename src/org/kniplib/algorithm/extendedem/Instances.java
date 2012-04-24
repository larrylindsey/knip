package org.kniplib.algorithm.extendedem;

import java.io.Serializable;
import java.util.ArrayList;

public class Instances extends ArrayList<Instance> implements Serializable {
        protected int m_ClassIndex;
        protected String m_RelationName;
        protected ArrayList<Instance> m_Instances;
        protected ArrayList<Attribute> m_Attributes;

        public Instances(final Instances dataset, final int capacity) {
                initialize(dataset, capacity);
        }

        public Instances(final String name, final ArrayList<Attribute> attInfo,
                        final int capacity) {

                m_RelationName = name;
                m_ClassIndex = -1;
                m_Attributes = attInfo;
                for (int i = 0; i < numAttributes(); i++) {
                        attribute(i).setIndex(i);
                }
                m_Instances = new ArrayList<Instance>(capacity);
        }

        protected void initialize(final Instances dataset, int capacity) {
                if (capacity < 0)
                        capacity = 0;

                // Strings only have to be "shallow" copied because
                // they can't be modified.
                m_ClassIndex = dataset.m_ClassIndex;
                m_RelationName = dataset.m_RelationName;
                m_Attributes = dataset.m_Attributes;
                m_Instances = new ArrayList<Instance>(capacity);
        }

        @Override
        public Instance get(final int index) {
                return m_Instances.get(index);
        }

        public Instance instance(final int index) {
                return m_Instances.get(index);
        }

        public Attribute attribute(final int index) {

                return m_Attributes.get(index);
        }

        public int numAttributes() {

                return m_Attributes.size();
        }

        @Override
        public boolean add(final Instance inst){
                return m_Instances.add(inst);
        }

        public int maxIndex(final double[] doubles) {

                double maximum = 0;
                int maxIndex = 0;

                for (int i = 0; i < doubles.length; i++) {
                        if ((i == 0) || (doubles[i] > maximum)) {
                                maxIndex = i;
                                maximum = doubles[i];
                        }
                }

                return maxIndex;
        }

        public int maxIndex(final int[] ints) {

                int maximum = 0;
                int maxIndex = 0;

                for (int i = 0; i < ints.length; i++) {
                        if ((i == 0) || (ints[i] > maximum)) {
                                maxIndex = i;
                                maximum = ints[i];
                        }
                }

                return maxIndex;
        }

        private int partition(final int[] array, final int[] index, int l, int r) {

                final double pivot = array[index[(l + r) / 2]];
                int help;

                while (l < r) {
                        while ((array[index[l]] < pivot) && (l < r)) {
                                l++;
                        }
                        while ((array[index[r]] > pivot) && (l < r)) {
                                r--;
                        }
                        if (l < r) {
                                help = index[l];
                                index[l] = index[r];
                                index[r] = help;
                                l++;
                                r--;
                        }
                }
                if ((l == r) && (array[index[r]] > pivot)) {
                        r--;
                }

                return r;
        }

        private void quickSort(final int[] array, final int[] index,
                        final int left, final int right) {

                if (left < right) {
                        final int middle = partition(array, index, left, right);
                        quickSort(array, index, left, middle);
                        quickSort(array, index, middle + 1, right);
                }
        }

        public int[] sort(final int[] array) {

                final int[] index = new int[array.length];
                final int[] newIndex = new int[array.length];
                int[] helpIndex;
                int numEqual;

                for (int i = 0; i < index.length; i++) {
                        index[i] = i;
                }
                quickSort(array, index, 0, array.length - 1);

                // Make sort stable
                int i = 0;
                while (i < index.length) {
                        numEqual = 1;
                        for (int j = i + 1; ((j < index.length) && (array[index[i]] == array[index[j]])); j++) {
                                numEqual++;
                        }
                        if (numEqual > 1) {
                                helpIndex = new int[numEqual];
                                for (int j = 0; j < numEqual; j++) {
                                        helpIndex[j] = i + j;
                                }
                                quickSort(index, helpIndex, 0, numEqual - 1);
                                for (int j = 0; j < numEqual; j++) {
                                        newIndex[i + j] = index[helpIndex[j]];
                                }
                                i += numEqual;
                        } else {
                                newIndex[i] = index[i];
                                i++;
                        }
                }
                return newIndex;
        }

        public int[] sort(double[] array) {

                final int[] index = new int[array.length];
                array = array.clone();
                for (int i = 0; i < index.length; i++) {
                        index[i] = i;
                        if (Double.isNaN(array[i])) {
                                array[i] = Double.MAX_VALUE;
                        }
                }
                quickSort(array, index, 0, array.length - 1);
                return index;
        }

        private int partition(final double[] array, final int[] index, int l,
                        int r) {

                final double pivot = array[index[(l + r) / 2]];
                int help;

                while (l < r) {
                        while ((array[index[l]] < pivot) && (l < r)) {
                                l++;
                        }
                        while ((array[index[r]] > pivot) && (l < r)) {
                                r--;
                        }
                        if (l < r) {
                                help = index[l];
                                index[l] = index[r];
                                index[r] = help;
                                l++;
                                r--;
                        }
                }
                if ((l == r) && (array[index[r]] > pivot)) {
                        r--;
                }

                return r;
        }

        private void quickSort(final double[] array, final int[] index,
                        final int left, final int right) {

                if (left < right) {
                        final int middle = partition(array, index, left, right);
                        quickSort(array, index, left, middle);
                        quickSort(array, index, middle + 1, right);
                }
        }

        public double meanOrMode(final int attIndex) {

                double result, found;
                int[] counts;

                if (attribute(attIndex).isNumeric()) {
                        result = found = 0;
                        for (int j = 0; j < numInstances(); j++) {
                                if (!instance(j).isMissing(attIndex)) {
                                        found += instance(j).weight();
                                        result += instance(j).weight()
                                        * instance(j).value(
                                                        attIndex);
                                }
                        }
                        if (found <= 0) {
                                return 0;
                        } else {
                                return result / found;
                        }
                } else if (attribute(attIndex).isNominal()) {
                        counts = new int[attribute(attIndex).numValues()];
                        for (int j = 0; j < numInstances(); j++) {
                                if (!instance(j).isMissing(attIndex)) {
                                        counts[(int) instance(j)
                                               .value(attIndex)] += instance(
                                                               j).weight();
                                }
                        }
                        return maxIndex(counts);
                } else {
                        return 0;
                }
        }

        public/* @pure@ */double[] attributeToDoubleArray(final int index) {

                final double[] result = new double[numInstances()];
                for (int i = 0; i < result.length; i++) {
                        result[i] = instance(i).value(index);
                }
                return result;
        }

        public AttributeStats attributeStats(final int index) {

                final AttributeStats result = new AttributeStats();
                if (attribute(index).isNominal()) {
                        result.nominalCounts = new int[attribute(index)
                                                       .numValues()];
                        result.nominalWeights = new double[attribute(index)
                                                           .numValues()];
                }
                if (attribute(index).isNumeric()) {
                        result.numericStats = new Stats();
                }
                result.totalCount = numInstances();

                final double[] attVals = attributeToDoubleArray(index);
                final int[] sorted = sort(attVals);
                int currentCount = 0;
                double currentWeight = 0;
                double prev = Double.NaN;
                for (int j = 0; j < numInstances(); j++) {
                        final Instance current = instance(sorted[j]);
                        if (current.isMissing(index)) {
                                result.missingCount = numInstances() - j;
                                break;
                        }
                        if (current.value(index) == prev) {
                                currentCount++;
                                currentWeight += current.weight();
                        } else {
                                result.addDistinct(prev, currentCount,
                                                currentWeight);
                                currentCount = 1;
                                currentWeight = current.weight();
                                prev = current.value(index);
                        }
                }
                result.addDistinct(prev, currentCount, currentWeight);
                result.distinctCount--; // So we don't count "missing" as a
                // value
                return result;
        }

        @Override
        public int size() {
                return m_Instances.size();
        }

        public int numInstances() {
                return m_Instances.size();
        }

}