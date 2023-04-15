package de.joshicodes.rja.rest;

import com.google.gson.JsonArray;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.object.user.UserStatus;
import de.joshicodes.rja.requests.rest.user.self.EditSelfUserRequest;

public class EditSelfRestAction extends RestAction<User> {

    private final EditSelfUserRequest request;

    public EditSelfRestAction(RJA rja) {
        super(rja);
        request = new EditSelfUserRequest(rja);
    }

    public EditSelfRestAction remove(String... fields) {
        JsonArray array = new JsonArray();
        for (String field : fields) {
            array.add(field);
        }
        request.remove(array);
        return this;
    }

    public EditSelfRestAction setStatus(final String text, final UserStatus.Presence presence) {
        request.setStatus(new UserStatus() {
            @Override
            public String text() {
                return text;
            }

            @Override
            public Presence presence() {
                return presence;
            }
        });
        return this;
    }

    @Override
    protected User execute() {
        return getRJA().getRequestHandler().sendRequest(getRJA(), request);
    }

}
