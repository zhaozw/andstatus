/*
* Copyright (C) 2013 yvolk (Yuri Volkov), http://yurivolkov.com
* Based on the sample: com.example.android.samplesync.syncadapter
* Copyright (C) 2010 The Android Open Source Project
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

package org.andstatus.app.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import org.andstatus.app.account.MyAccount;
import org.andstatus.app.account.MyAccount.CredentialsVerificationStatus;
import org.andstatus.app.context.MyContextHolder;
import org.andstatus.app.data.TimelineTypeEnum;
import org.andstatus.app.service.CommandData;
import org.andstatus.app.service.CommandEnum;
import org.andstatus.app.service.MyService;
import org.andstatus.app.service.MyServiceListener;
import org.andstatus.app.service.MyServiceManager;
import org.andstatus.app.service.MyServiceReceiver;
import org.andstatus.app.util.MyLog;

/**
 * SyncAdapter implementation. Its only purpose for now is to properly initialize {@link MyService}.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter implements MyServiceListener {

    private final Context context;
    private volatile CommandData commandData;
    private volatile boolean syncCompleted = false;
    private Object syncLock = new Object();
    private volatile SyncResult syncResult;
    
    private MyServiceReceiver intentReceiver;
    
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        MyLog.d(this, "created, context=" + context.getClass().getCanonicalName());
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {
        String method = "onPerformSync";
        if (!MyServiceManager.isServiceAvailable()) {
            syncResult.stats.numIoExceptions++;
            MyLog.d(this, method + " Service not available, account=" + account.name);
            return;
        }
        MyContextHolder.initialize(context, this);
        if (!MyContextHolder.get().isReady()) {
            syncResult.stats.numIoExceptions++;
            MyLog.d(this, method + " Context is not ready, account=" + account.name);
            return;
        }
        MyAccount ma = MyContextHolder.get().persistentAccounts().fromAccountName(account.name);
        if (ma == null) {
            MyLog.d(this, method + " The account was not loaded, account=" + account.name);
            return;
            
        } else if (ma.getCredentialsVerified() != CredentialsVerificationStatus.SUCCEEDED) {
            MyLog.d(this, method + " Credentials failed, skipping; account=" + account.name);
            return;
        }
        intentReceiver = new MyServiceReceiver(this);
        syncCompleted = false;
        try {
            this.syncResult = syncResult;
            MyLog.d(this, method + " started, account=" + account.name);
            intentReceiver.registerReceiver(context);
            commandData = new CommandData(CommandEnum.AUTOMATIC_UPDATE, account.name,
                    TimelineTypeEnum.ALL, 0);
            MyServiceManager.sendCommand(commandData);
            synchronized(syncLock) {
                for (int iteration = 0; iteration < 10; iteration++) {
                    if (syncCompleted) {
                        break;
                    }
                    syncLock.wait(java.util.concurrent.TimeUnit.SECONDS.toMillis(30));
                }
            }
            MyLog.d(this, method + " ended, " + (syncResult.hasError() ? "has error" : "ok"));
        } catch (InterruptedException e) {
            MyLog.d(this, method + " interrupted", e);
        } finally {
            intentReceiver.unregisterReceiver(context);            
        }
    }

    @Override
    public void onReceive(CommandData commandData) {
        MyLog.d(this, "onReceive, command=" + commandData.getCommand());
        synchronized (syncLock) {
            if (this.commandData != null && this.commandData.equals(commandData)) {
                syncCompleted = true;
                syncResult.stats.numAuthExceptions += commandData.getResult().getNumAuthExceptions();
                syncResult.stats.numIoExceptions += commandData.getResult().getNumIoExceptions();
                syncResult.stats.numParseExceptions += commandData.getResult().getNumParseExceptions();
                syncLock.notifyAll();
            }
        }
    }
}
