/*
 * Copyright (C) 2017  Ľuboš Kozmon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elkozmon.zoonavigator.core.action.actions

import com.elkozmon.zoonavigator.core.action.ActionHandler
import com.elkozmon.zoonavigator.core.curator.BackgroundOps
import com.elkozmon.zoonavigator.core.zookeeper.znode._
import org.apache.curator.framework.CuratorFramework

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.Future

class GetZNodeChildrenActionHandler(
    curatorFramework: CuratorFramework,
    implicit val executionContextExecutor: ExecutionContextExecutor
) extends ActionHandler[GetZNodeChildrenAction]
    with BackgroundOps {

  override def handle(
      action: GetZNodeChildrenAction
  ): Future[ZNodeMetaWith[ZNodeChildren]] =
    curatorFramework.getChildren
      .forPathBackground(action.path.path)
      .map { event =>
        val path = event.getPath.stripSuffix("/")
        val meta = ZNodeMeta.fromStat(event.getStat)
        val children = ZNodeChildren(
          event.getChildren.asScala
            .map(name => ZNodePath(s"$path/$name"))
            .toList
        )

        ZNodeMetaWith(children, meta)
      }
}