package org.knime.knip.core.data.graphtheory;

/**
 * Class to represent pixel coherence as directed edges in the graph.
 * 
 * This implementation was <b>heavily</b> inspired by the implementation provided by Kolmogorov and Boykov: MAXFLOW
 * version 3.01.
 * 
 * From the README of the library:
 * 
 * This software library implements the maxflow algorithm described in
 * 
 * "An Experimental Comparison of Min-Cut/Max-Flow Algorithms for Energy Minimization in Vision." Yuri Boykov and
 * Vladimir Kolmogorov. In IEEE Transactions on Pattern Analysis and Machine Intelligence (PAMI), September 2004
 * 
 * This algorithm was developed by Yuri Boykov and Vladimir Kolmogorov at Siemens Corporate Research. To make it
 * available for public use, it was later reimplemented by Vladimir Kolmogorov based on open publications.
 * 
 * If you use this software for research purposes, you should cite the aforementioned paper in any resulting
 * publication.
 * 
 * @author Jan Funke <jan.funke@inf.tu-dresden.de>
 * @version 0.1
 */
public class Edge {

    // node the edge points to
    private Node m_head;

    // nect edge with the same originating node
    private Edge m_next;

    // reverse arc
    private Edge m_sister;

    // residual capacity of this edge
    private float m_residualCapacity;

    // special edges
    public final static Edge TERMINAL = new Edge();

    public final static Edge ORPHAN = new Edge();

    public Edge() {

        m_head = null;
        m_next = null;
        m_sister = null;

        m_residualCapacity = 0;
    }

    /**
     * Gets the head, i.e., the node this edge points to for this edge.
     * 
     * @return The head, i.e., the node this edge points to.
     */
    public Node getHead() {
        return this.m_head;
    }

    /**
     * Sets the head for this edge.
     * 
     * @param head The head, i.e., the node this edge shall point to.
     */
    public void setHead(final Node head) {
        this.m_head = head;
    }

    /**
     * Sets the next edge for this edge.
     * 
     * @param next The next edge.
     */
    public void setNext(final Edge next) {
        this.m_next = next;
    }

    /**
     * Sets the sister for this instance.
     * 
     * @param sister The sister.
     */
    public void setSister(final Edge sister) {
        this.m_sister = sister;
    }

    /**
     * Sets the residual capacity for this instance.
     * 
     * @param residualCapacity The residual capacity.
     */
    public void setResidualCapacity(final float residualCapacity) {
        this.m_residualCapacity = residualCapacity;
    }

    /**
     * Gets the residual capacity for this instance.
     * 
     * @return The residualCapacity.
     */
    public float getResidualCapacity() {
        return this.m_residualCapacity;
    }

    /**
     * Gets the sister for this instance.
     * 
     * @return The sister.
     */
    public Edge getSister() {
        return this.m_sister;
    }

    /**
     * Gets the next for this instance.
     * 
     * @return The next.
     */
    public Edge getNext() {
        return this.m_next;
    }
}
