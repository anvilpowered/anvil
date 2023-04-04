package org.anvilpowered.anvil.ui

import mui.material.Typography
import org.anvilpowered.anvil.ui.page.dashboard.DashboardHome
import org.anvilpowered.anvil.ui.page.servers.ServersCreate
import org.anvilpowered.anvil.ui.page.servers.ServersHome
import react.FC
import react.Props
import react.create
import react.router.Navigate
import react.router.Route
import react.router.Routes

val Routing = FC<Props> {
    Routes {
        Route {
            path = "/dashboard"
            element = DashboardHome.create()
        }
        Route {
            path = "/servers"
            element = ServersHome.create()
        }
        Route {
            path = "/servers/create"
            element = ServersCreate.create()
        }
        Route {
            path = "/"
            element = Navigate.create {
                to = "/dashboard"
            }
        }
        Route {
            path = "*"
            element = Typography.create { +"404 page not found" }
        }
    }
}
