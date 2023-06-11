package de.joshicodes.rja.rest;

import com.google.gson.JsonArray;
import de.joshicodes.rja.RJA;
import de.joshicodes.rja.object.user.User;
import de.joshicodes.rja.object.user.UserStatus;
import de.joshicodes.rja.requests.rest.user.self.EditSelfUserRequest;
import de.joshicodes.rja.util.Pair;

public class EditSelfRestAction extends SimpleRestAction<User> {

    private final EditSelfUserRequest request;

    public EditSelfRestAction(RJA rja) {
        super(rja, () -> null);
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
    protected Pair<Long, User> execute() throws Exception {
        super.request = () -> request;
        return super.execute();
    }

}
