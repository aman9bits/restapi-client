/*
 * Copyright 2012-2015, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.aesop.serializer.stateengine;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.flipkart.aesop.runtime.producer.ReadEventCycleSummary;
import com.netflix.zeno.diff.DiffSerializationFramework;
import com.netflix.zeno.diff.TypeDiff;
import com.netflix.zeno.diff.TypeDiffInstruction;
import com.netflix.zeno.diff.TypeDiffOperation;
import com.netflix.zeno.fastblob.FastBlobStateEngine;
import com.netflix.zeno.fastblob.state.TypeDeserializationStateListener;
import com.netflix.zeno.serializer.SerializerFactory;

/**
 * The <code>DiffInterpreter</code> loads serialized data snapshots and deltas onto a {@link FastBlobStateEngine} and provides methods to listen-in on the 
 * change that the state engine goes through.
 * 
 * @author Regunath B
 * @version 1.0, 17 March 2014
 */
public abstract class DiffInterpreter<T, S extends GenericRecord> implements InitializingBean {
	
	/** The StateTransitioner instance */
	protected SerializerFactory serializerFactory;
	
	/** The file location for reading snapshots and deltas */
	protected String serializedDataLocation;
	
	/** The DiffChangeEventMapper instance for mapping state engine changes to change events*/
	protected DiffChangeEventMapper<T,S> diffChangeEventMapper;
	
	/** The FastBlobStateEngine that this DiffInterpreter creates and manages */
	private FastBlobStateEngine stateEngine;	

	/**
	 * Interface method implementation. Checks for mandatory dependencies
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.serializerFactory,"'serializerFactory' cannot be null. This diff interpreter will not be initialized");
		Assert.notNull(this.serializedDataLocation,"'serializedDataLocation' cannot be null. This diff interpreter will not be initialized");
		Assert.notNull(this.diffChangeEventMapper,"'diffChangeEventMapper' cannot be null. This diff interpreter will not be initialized");
		this.stateEngine = new FastBlobStateEngine(this.serializerFactory);		
	}
	
	/**
	 * Gets change events that occurred after the specified SCN
	 * @param sinceSCN the last seen SCN
	 * @return ReadEventCycleSummary containing change events and the new SCN
	 */
	public ReadEventCycleSummary<S> getChangeEvents(long sinceSCN) {
		if (this.stateEngine.getLatestVersion() == null || Long.valueOf(this.stateEngine.getLatestVersion()) < sinceSCN) { 
			this.readSnapshotAndDeltasForSCN(this.stateEngine, sinceSCN); // bring up the state engine to the SCN
		}
		DiffTypeDeserializationStateListener diffTypeDeserializationStateListener = new DiffTypeDeserializationStateListener();
		this.stateEngine.setTypeDeserializationStateListener(this.diffChangeEventMapper.getNFTypeName(),diffTypeDeserializationStateListener);
		this.readSnapshotAndDeltasAfterSCN(this.stateEngine, sinceSCN);
		DiffSerializationFramework diffSerializationFramework = new DiffSerializationFramework(this.serializerFactory);
		TypeDiffInstruction<T> diffInstruction = new TypeDiffInstruction<T>() {
            public String getSerializerName() {
                return diffChangeEventMapper.getNFTypeName();
            }
            public Object getKey(T object) {
                return diffChangeEventMapper.getTypeKey(object);
            }
        };		
        TypeDiffOperation<T> diffOperation = new TypeDiffOperation<T>(diffInstruction);
		TypeDiff<T> typeDiff = diffOperation.performDiff(diffSerializationFramework, 
				diffTypeDeserializationStateListener.getRemovedObjectsList(), diffTypeDeserializationStateListener.getAddedObjectsList());
		return new ReadEventCycleSummary<S>(this.diffChangeEventMapper.getChangeEvents(typeDiff), Long.valueOf(this.stateEngine.getLatestVersion()));
	}
	
	/**
	 * Reads snapshots and deltas that bring up the stateEngine version to the specified SCN
	 * @param stateEngine the FastBlobStateEngine that is to be initialized with data reflecting the specified SCN 
	 * @param sinceSCN the last seen SCN
	 */
	protected abstract void readSnapshotAndDeltasForSCN(FastBlobStateEngine stateEngine, long sinceSCN);

	/**
	 * Appends snapshots and deltas to the stateEngine for state changes that occurred after specified SCN
	 * @param stateEngine the FastBlobStateEngine to append snapshots and deltas to 
	 * @param sinceSCN the last seen SCN
	 */
	protected abstract void readSnapshotAndDeltasAfterSCN(FastBlobStateEngine stateEngine, long sinceSCN);
	
	/** Getter/Setter methods */
	public void setSerializerFactory(SerializerFactory serializerFactory) {
		this.serializerFactory = serializerFactory;
	}
	public void setSerializedDataLocation(String serializedDataLocation) {
		this.serializedDataLocation = serializedDataLocation;
	}			
	public String getSerializedDataLocation() {
		return serializedDataLocation;
	}
	public void setDiffChangeEventMapper(DiffChangeEventMapper<T, S> diffChangeEventMapper) {
		this.diffChangeEventMapper = diffChangeEventMapper;
	}	
	
	/**
	 * Listens into the Type deserialization state as snapshots and deltas are loaded on to the state engine. Keeps tracks of state changes
	 * that is then used to create change events
	 */
	private class DiffTypeDeserializationStateListener extends TypeDeserializationStateListener<T> {

		private List<T> addedObjectsList = new ArrayList<T>();
		private List<T> removedObjectsList = new ArrayList<T>();
		
		public void addedObject(T object, int ordinal) {
			this.addedObjectsList.add(object);
		}
		public void removedObject(T object, int ordinal) {
			this.removedObjectsList.add(object);
		}		
		public void reassignedObject(T object, int oldOrdinal, int newOrdinal) {
		}
		public List<T> getAddedObjectsList() {
			return addedObjectsList;
		}
		public List<T> getRemovedObjectsList() {
			return removedObjectsList;
		}

	}

}