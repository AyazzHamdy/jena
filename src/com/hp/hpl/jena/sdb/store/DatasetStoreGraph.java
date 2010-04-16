/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sdb.store;

import java.util.Iterator ;

import com.hp.hpl.jena.graph.Graph ;
import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.sdb.Store ;
import com.hp.hpl.jena.sdb.graph.GraphSDB ;
import com.hp.hpl.jena.sdb.shared.SDBNotImplemented ;
import com.hp.hpl.jena.sdb.util.StoreUtils ;
import com.hp.hpl.jena.shared.Lock ;
import com.hp.hpl.jena.shared.LockMRSW ;
import com.hp.hpl.jena.sparql.core.Closeable ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.core.DatasetImpl ;
import com.hp.hpl.jena.sparql.util.Context ;
import com.hp.hpl.jena.update.GraphStore ;

/** A graph-level dataset for SDB - triggers SDB SQL processing when used in a query */
public class DatasetStoreGraph implements DatasetGraph, Closeable, GraphStore //implements DatasetGraph
{
    final Store store ;
    Graph defaultGraph = null ;
    Lock lock = new LockMRSW() ;
    final Context context ;
    
    public DatasetStoreGraph(Store store, Context context)
    {
        this(store, null, context) ;
    }
    
    public DatasetStoreGraph(Store store, GraphSDB graph, Context context)
    {
        this.store = store ;
        // Force the "default" graph
        this.defaultGraph = graph ;
        this.context = context ;
    }

//    public DatasetStoreGraph(StoreRDB store, Context context)
//    {
//        this.store = store ; 
//        this.defaultGraph = SDBFactory.connectDefaultGraph(store) ;
//    }
    
    public Store getStore() { return store ; }

    public Context getContext()
    {
        return context ;
    }

    public Iterator<Node> listGraphNodes()
    {
        return StoreUtils.storeGraphNames(store) ;//.iterator() ;
    }

    public Lock getLock() { return lock ; }

    public boolean containsGraph(Node gn)
    {
        // Think about this ... 
        throw new SDBNotImplemented("DatasetStore.containsGraph") ;
        //return false ;
    }

    public Graph getDefaultGraph()
    {
        if ( defaultGraph == null )
            defaultGraph = new GraphSDB(store) ;
        return defaultGraph ;
    }

    public Graph getGraph(Node gn)
    {
        return new GraphSDB(store, gn) ;
    }
    
    public int size() { return -1 ; }

    public void close()
    { store.close(); }

    //---- Update
    public void startRequest()
    {}

    public void finishRequest()
    {}

    public Dataset toDataset()
    { return new DatasetImpl(this) ; }

    public void addGraph(Node graphName, Graph graph)
    {
        Graph g = getGraph(graphName) ;
        g.getBulkUpdateHandler().add(graph) ;
    }

    public Graph removeGraph(Node graphName)
    {
        Graph g = getGraph(graphName) ;
        g.getBulkUpdateHandler().removeAll() ;
        // Return null (it's empty!)
        return null ;
    }

    public void setDefaultGraph(Graph g)
    { throw new UnsupportedOperationException("Can't set default graph via GraphStore on a SDB-backed dataset") ; }  
}

/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */