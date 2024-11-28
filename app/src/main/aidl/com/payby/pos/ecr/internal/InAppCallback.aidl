// InAppCallback.aidl
package com.payby.pos.ecr.internal;

// Declare any non-default types here with import statements

interface InAppCallback {

    oneway void onReceive(in byte[] bytes);

}