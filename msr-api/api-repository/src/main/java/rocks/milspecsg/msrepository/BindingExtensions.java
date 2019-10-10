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
import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.lang.annotation.Annotation;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class BindingExtensions {

    private final Binder binder;

    public BindingExtensions(Binder binder) {
        this.binder = binder;
    }

    public <TDbo extends ObjectWithId<?>,
        From1 extends Repository<?, ?, ?, ?>,
        From2 extends Repository<?, TDbo, ?, ?>,
        Target extends From1>
    void bind(
        TypeToken<From1> from1,
        TypeToken<From2> from2,
        TypeToken<Target> target,
        Class<? extends Annotation> repoAnnotation
    ) {
        binder.bind((TypeLiteral<From1>) TypeLiteral.get(from2.getType())).annotatedWith(repoAnnotation).to((TypeLiteral<Target>) TypeLiteral.get(target.getType()));
    }

    public <From, Target extends From> void bind(
        TypeToken<From> from,
        TypeToken<Target> target
    ) {
        binder.bind((TypeLiteral<From>) TypeLiteral.get(from.getType())).to((TypeLiteral<Target>) TypeLiteral.get(target.getType()));
    }
}
