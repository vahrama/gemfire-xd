/*
 * Copyright (c) 2010-2015 Pivotal Software, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */

package com.pivotal.gemfirexd.execute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.gemstone.gemfire.cache.CacheCallback;
import com.pivotal.gemfirexd.callbacks.DBSynchronizer;
import com.pivotal.gemfirexd.callbacks.Event;

/**
 * Observer to enable intercepting queries at different points of execution.
 * 
 * @author swale
 */
public interface QueryObserver extends CacheCallback {

  /**
   * Invoked by {@link DBSynchronizer} after a primary based DML event is
   * processed.
   * 
   * @param type
   *          the {@link Event.Type} of the event
   * @param numRowsModified
   *          the number of rows modified in the processing of this statement on
   *          backend database by {@link DBSynchronizer}
   * @param stmt
   *          handle to the {@link Statement} used for executing on the backed
   *          database by {@link DBSynchronizer}
   */
  public void afterPKBasedDBSynchExecution(Event.Type type,
      int numRowsModified, Statement stmt);

  /**
   * Invoked by {@link DBSynchronizer} after a bulk DML or bulk insert event is
   * processed.
   * 
   * @param type
   *          the {@link Event.Type} of the event
   * @param numRowsModified
   *          the number of rows modified in the processing of this statement on
   *          backend database by {@link DBSynchronizer}
   * @param stmt
   *          handle to the {@link Statement} used for executing on the backed
   *          database by {@link DBSynchronizer}
   * @param sql
   *          the SQL string for this event
   */
  public void afterBulkOpDBSynchExecution(Event.Type type, int numRowsModified,
      Statement stmt, String sql);

  /**
   * Invoked by {@link DBSynchronizer} after commit is successfully executed on
   * the backend database for a batch of events.
   * 
   * @param batchProcessed
   *          the batch of events committed to the backend database
   */
  public void afterCommitDBSynchExecution(List<Event> batchProcessed);

  /**
   * Callback invoked after an unsuccessful prepare of a
   * {@link PreparedStatement}.
   * 
   * @param conn
   *          the current server connection on which query is being prepared
   * @param sql
   *          the SQL statement being prepared
   * @param resultSetType
   *          one of the following <code>ResultSet</code> constants:
   *          <code>ResultSet.TYPE_FORWARD_ONLY</code>,
   *          <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
   *          <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
   * @param resultSetConcurrency
   *          one of the following <code>ResultSet</code> constants:
   *          <code>ResultSet.CONCUR_READ_ONLY</code> or
   *          <code>ResultSet.CONCUR_UPDATABLE</code>
   * @param resultSetHoldability
   *          one of the following <code>ResultSet</code> constants:
   *          <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
   *          <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
   * @param autoGeneratedKeys
   *          a flag indicating whether auto-generated keys should be returned;
   *          one of <code>Statement.RETURN_GENERATED_KEYS</code> or
   *          <code>Statement.NO_GENERATED_KEYS</code>
   * @param columnIndexes
   *          an array of column indexes indicating the columns that should be
   *          returned from the inserted row or rows
   * @param columnNames
   *          an array of column names indicating the columns that should be
   *          returned from the inserted row or rows
   * @param sqle
   *          the exception thrown during the prepare of current statement
   * 
   * @return if non-null then the new {@link PreparedStatement} object to be
   *         used internally
   */
  public PreparedStatement afterQueryPrepareFailure(Connection conn,
      String sql, int resultSetType, int resultSetConcurrency,
      int resultSetHoldability, int autoGeneratedKeys, int[] columnIndexes,
      String[] columnNames, SQLException sqle) throws SQLException;

  /**
   * Callback invoked after end of a query execution (successful or
   * unsuccessful).
   * 
   * @param stmt
   *          the {@link CallbackStatement} for the current query
   * @param sqle
   *          non-null is any exception was thrown during the execution of
   *          current statement
   * 
   * @return true if the thrown SQLException has to be ignored and instead
   *         result set by the observer using
   *         {@link CallbackStatement#setResultSet} or update count using
   *         {@link CallbackStatement#setUpdateCount} has to be returned; false
   *         indicates no change to end result
   */
  public boolean afterQueryExecution(CallbackStatement stmt, SQLException sqle)
      throws SQLException;

  /**
   * Invoked after commit on a connection is complete.
   */
  public void afterCommit(Connection conn);

  /**
   * Invoked after rollback on a connection is complete.
   */
  public void afterRollback(Connection conn);

  /**
   * Calback invoked after successfully executing connection close.
   */
  public void afterConnectionClose(Connection conn);
}