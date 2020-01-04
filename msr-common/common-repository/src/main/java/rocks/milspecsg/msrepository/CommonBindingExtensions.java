/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rocks.milspecsg.msrepository;

import com.google.common.reflect.TypeToken;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import rocks.milspecsg.msrepository.api.component.Component;

import java.lang.annotation.Annotation;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class CommonBindingExtensions implements BindingExtensions {

    private final Binder binder;

    public CommonBindingExtensions(Binder binder) {
        this.binder = binder;
    }

    @Override
    public <From1 extends Component<?, ?, ?>,
        From2 extends Component<?, ?, ?>,
        From3 extends From1,
        Target extends From1>
    void bind(
        TypeToken<From1> from1,
        TypeToken<From2> from2,
        TypeToken<From3> from3,
        TypeToken<Target> target,
        Class<? extends Annotation> componentAnnotation
    ) {
        binder.bind((TypeLiteral<From1>) TypeLiteral.get(from2.getType()))
            .annotatedWith(componentAnnotation)
            .to((TypeLiteral<Target>) TypeLiteral.get(target.getType()));

        binder.bind((TypeLiteral<From1>) TypeLiteral.get(from3.getType()))
            .to((TypeLiteral<Target>) TypeLiteral.get(target.getType()));
    }

    @Override
    public <From1 extends Component<?, ?, ?>,
        From2 extends From1,
        Target extends From1>
    void bind(
        TypeToken<From1> from1,
        TypeToken<From2> from2,
        TypeToken<Target> target,
        Class<? extends Annotation> componentAnnotation
    ) {
        bind(from1, from1, from2, target, componentAnnotation);
    }

    @Override
    public <From, Target extends From> void bind(
        TypeToken<From> from,
        TypeToken<Target> target,
        Class<? extends Annotation> annotation
    ) {
        binder.bind((TypeLiteral<From>) TypeLiteral.get(from.getType()))
            .annotatedWith(annotation)
            .to((TypeLiteral<Target>) TypeLiteral.get(target.getType()));
    }

    @Override
    public <From, Target extends From> void bind(
        TypeToken<From> from,
        TypeToken<Target> target
    ) {
        binder.bind((TypeLiteral<From>) TypeLiteral.get(from.getType()))
            .to((TypeLiteral<Target>) TypeLiteral.get(target.getType()));
    }
}
