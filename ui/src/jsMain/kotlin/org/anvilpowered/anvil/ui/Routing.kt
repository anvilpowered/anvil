package org.anvilpowered.anvil.ui

import mui.material.Typography
import org.anvilpowered.anvil.ui.page.Dashboard
import org.anvilpowered.anvil.ui.page.Servers
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
            element = Dashboard.create()
        }
        Route {
            path = "/servers"
            element = Servers.create()
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
