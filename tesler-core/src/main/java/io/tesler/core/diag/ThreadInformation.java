/*-
 * #%L
 * IO Tesler - Core
 * %%
 * Copyright (C) 2018 - 2019 Tesler Contributors
 * %%
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
 * #L%
 */

package io.tesler.core.diag;

import io.tesler.core.diag.jdbc.ConnectionInfo;
import io.tesler.core.diag.jdbc.ConnectionRegistry;
import io.tesler.core.util.SpringBeanUtils;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ThreadInformation implements Comparable<ThreadInformation> {

	private final long threadID;

	private final ThreadInfo threadInfo;

	private final String name;

	private final List<ConnectionInfo> connections;

	public ThreadInformation(ThreadInfo info, List<ConnectionInfo> connections) {
		threadInfo = info;
		name = info.getThreadName();
		threadID = info.getThreadId();
		this.connections = connections;
	}

	public static List<ThreadInformation> getThreads(boolean deadlock) {
		Map<Long, List<ConnectionInfo>> connections = SpringBeanUtils.getBean(ConnectionRegistry.class)
				.getConnections().stream()
				.collect(Collectors.groupingBy(ConnectionInfo::getThreadId));

		ThreadMXBean tmb = ManagementFactory.getThreadMXBean();
		List<ThreadInformation> threads = new ArrayList<>();
		long[] threadIds = null;
		if (deadlock) {
			threadIds = tmb.findDeadlockedThreads();
		} else {
			threadIds = tmb.getAllThreadIds();
		}
		if (threadIds == null) {
			return Collections.emptyList();
		}
		for (ThreadInfo info : tmb.getThreadInfo(threadIds, true, true)) {
			if (info == null) {
				continue;
			}
			threads.add(new ThreadInformation(info, connections.get(info.getThreadId())));
		}
		Collections.sort(threads);
		return threads;
	}

	public int compareTo(ThreadInformation o) {
		String myName = name + threadID;
		String yourName = o.name + o.threadID;
		return myName.compareTo(yourName);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("\"" + threadInfo.getThreadName() + "\"" +
				" Id=" + threadInfo.getThreadId() + " " +
				threadInfo.getThreadState());
		if (threadInfo.getLockName() != null) {
			sb.append(" on ").append(threadInfo.getLockName());
		}
		if (threadInfo.getLockOwnerName() != null) {
			sb.append(" owned by \"").append(threadInfo.getLockOwnerName()).append("\" Id=")
					.append(threadInfo.getLockOwnerId());
		}
		if (threadInfo.isSuspended()) {
			sb.append(" (suspended)");
		}
		if (threadInfo.isInNative()) {
			sb.append(" (in native)");
		}
		sb.append('\n');
		StackTraceElement[] stackTrace = threadInfo.getStackTrace();
		for (int i = 0, n = stackTrace.length; i < n; i++) {
			StackTraceElement ste = stackTrace[i];
			sb.append("\tat ").append(ste).append('\n');
			if (i == 0 && threadInfo.getLockInfo() != null) {
				Thread.State ts = threadInfo.getThreadState();
				switch (ts) {
					case BLOCKED:
						sb.append("\t-  blocked on ").append(threadInfo.getLockInfo()).append('\n');
						break;
					case WAITING:
					case TIMED_WAITING:
						sb.append("\t-  waiting on ").append(threadInfo.getLockInfo()).append('\n');
						break;
					default:
				}
			}

			for (MonitorInfo mi : threadInfo.getLockedMonitors()) {
				if (mi.getLockedStackDepth() == i) {
					sb.append("\t-  locked ").append(mi).append('\n');
				}
			}
		}

		LockInfo[] locks = threadInfo.getLockedSynchronizers();
		if (locks.length > 0) {
			sb.append("\n\tNumber of locked synchronizers = ").append(locks.length).append('\n');
			Arrays.stream(locks).forEach(li -> {
						sb.append("\t- ").append(li).append('\n');
					}
			);
		}

		if (connections != null && !connections.isEmpty()) {
			sb.append("\n\tNumber of database connections = ").append(connections.size()).append('\n');
			connections.forEach(c -> {
						sb.append("\t- id: ").append(c.getConnectionId());
						sb.append(", last sql: ").append(c.getLastSqlStatement());
						sb.append(", acquired at:").append('\n');
						Arrays.stream(c.getStackTrace()).forEach(e -> sb.append("\t\t at ").append(e).append('\n'));
					}
			);
		}

		sb.append('\n');
		return sb.toString();
	}

}
