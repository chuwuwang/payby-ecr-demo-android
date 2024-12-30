// InAppServiceEngine.aidl
package com.payby.pos.ecr.internal;

import com.payby.pos.ecr.internal.InAppCallback;

// Declare any non-default types here with import statements

interface InAppServiceEngine {

    oneway void send(in byte[] bytes, in InAppCallback callback);

    oneway void register(in InAppCallback callback);

    oneway void unregister();

}