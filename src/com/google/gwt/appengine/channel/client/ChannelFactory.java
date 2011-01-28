/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.gwt.appengine.channel.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.Timer;

/** Manages creating {@link Channel}s to receive messages from the server. */
public class ChannelFactory {

  private static final String CHANNEL_SRC = "/_ah/channel/jsapi";
  private static Channel channel;

  public static Channel createChannel(final String channelId) {
    if (channel == null) {
      ScriptElement script = Document.get().createScriptElement();
      script.setSrc(CHANNEL_SRC);
      Document.get().getElementsByTagName("head").getItem(0).appendChild(script);

      new Timer() {
        @Override
        public void run() {
          if (scriptLoaded()) {
            channel = createChannelImpl(channelId);
            this.cancel();
          }
        }
      }.scheduleRepeating(100);
    }
    return channel;
  }

  private static native boolean scriptLoaded() /*-{
    return (typeof $wnd.goog != 'undefined')
        && (typeof $wnd.goog.appengine != 'undefined')
        && (typeof $wnd.goog.appengine.Channel != 'undefined');
  }-*/;

  private static final native Channel createChannelImpl(String channelId) /*-{
    $wnd.__gwt_Channel = new $wnd.goog.appengine.Channel(channelId);
  }-*/;
}
