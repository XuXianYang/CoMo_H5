package com.dianxian.redis;

/**
 * Database Component:
 * 
 * name: com.lufax.database[.<database>.<schema>]
 * 
 * status list: 
 * 	NotInUse - not found the related component or not in use
 * 	Good - working fine
 * 	Warn - found some errors
 * 	Error - too many errors
 *  OutOfService -
 * 
 * JerseyClient Component:
 * 
 * name: com.lufax.jersey.client, jersey.client[.<server>]
 * 
 * status list: 
 * 	NotInUse - 
 * 	Good - 
 * 	Warn -
 * 	Error - 
 *  OutOfService -
 * 
 * 
 * Please use the proper combination of the defined status enums below for your component
 *
 */
public enum ComponentStatus {
	/**
	 * Not exist or not in use
	 */
	NotInUse,
	
	/**
	 * Under initializing
	 */
	Initializing,
	
	/**
	 * Already initialiazed, normally it should be in service
	 */
	Initialized,
	
	/**
	 * Working well
	 */
	Good,
	
	/**
	 * Some exception/errors occurred, but still be tolerable
	 */
	Warn,
	
	/**
	 * Too many exception/errors occurred, need to be taken care by operation
	 */
	Error,
	
	/**
	 * component is out of service
	 */
	OutOfService,
}
