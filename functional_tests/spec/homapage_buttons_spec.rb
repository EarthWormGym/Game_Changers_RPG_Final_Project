require "spec_helper"
feature "homepage buttons" do
  scenario "I should be able to go to sign in page" do
      visit '/home'
      click_button("Sign in")
      expect(page).to have_content 'Sign In to Battle!'
   end
    scenario "I should be able to go to battle page" do
        visit '/home'
        click_button("Battle")
        expect(page).to have_content 'Enemy'
     end
     scenario "I should be able to go to sign up page" do
         visit '/home'
         # find_button('Sign up', disabled: true)
         click_button("Sign up")
         expect(page).to have_content 'Sign Up for Battle!'
      end
end
